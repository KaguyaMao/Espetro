package org.espetro.support;

import java.util.*;

/**
 * Server-thread-only, round-scoped fire approval state. Independent of client and Minecraft
 * classes.
 */
public final class FireRequestBoard {
    public enum State {
        PENDING,
        EXECUTING,
        APPROVED,
        REJECTED,
        CANCELLED,
        EXPIRED;

        public boolean active() {
            return this == PENDING || this == EXECUTING;
        }
    }

    public record Actor(
            UUID id,
            String name,
            String team,
            int squad,
            boolean leader,
            boolean commander,
            boolean deployed) {
        public boolean member() {
            return id != null && ("ATTACK".equals(team) || "DEFEND".equals(team));
        }

        public boolean requester() {
            return member() && deployed && (leader || commander);
        }
    }

    public record Bounds(double minX, double minZ, double maxX, double maxZ) {
        public Bounds {
            if (!Double.isFinite(minX)
                    || !Double.isFinite(minZ)
                    || !Double.isFinite(maxX)
                    || !Double.isFinite(maxZ)
                    || minX >= maxX
                    || minZ >= maxZ) throw new IllegalArgumentException("地图范围无效");
        }

        public boolean contains(double x, double z) {
            return Double.isFinite(x)
                    && Double.isFinite(z)
                    && x >= minX
                    && x < maxX
                    && z >= minZ
                    && z < maxZ;
        }

        /** Same fixed 150 / 50 / (50/3) grid as ESPoints, anchored at the map's upper-left. */
        public String label(double x, double z) {
            if (!contains(x, z)) return "";
            double dx = x - minX, dz = z - minZ;
            long column = (long) Math.floor(dx / 150), row = (long) Math.floor(dz / 150);
            int mc = (int) ((long) Math.floor(dx / 50) % 3),
                    mr = (int) ((long) Math.floor(dz / 50) % 3);
            int sc = (int) ((long) Math.floor(dx * 3 / 50) % 3),
                    sr = (int) ((long) Math.floor(dz * 3 / 50) % 3);
            StringBuilder letters = new StringBuilder();
            for (long n = column + 1; n > 0; n = (n - 1) / 26)
                letters.append((char) ('A' + (n - 1) % 26));
            return "("
                    + letters.reverse()
                    + ","
                    + (row + 1)
                    + ","
                    + (mr * 3 + mc + 1)
                    + ","
                    + (sr * 3 + sc + 1)
                    + ")";
        }
    }

    public record Rules(
            int maximumPerTeam, long createInterval, long lifetime, double maximumLineLength) {
        public Rules {
            if (maximumPerTeam < 1
                    || maximumPerTeam > 96
                    || createInterval < 0
                    || lifetime < 20
                    || !Double.isFinite(maximumLineLength)
                    || maximumLineLength < 1) throw new IllegalArgumentException("火力申请配置无效");
        }

        public static Rules defaults() {
            return new Rules(48, 200, 6000, 1500);
        }
    }

    public record Request(
            UUID id,
            long session,
            State state,
            String team,
            String dimension,
            boolean line,
            double x,
            double z,
            double endX,
            double endZ,
            UUID requester,
            String requesterName,
            int squad,
            String note,
            String result,
            long created,
            long expires,
            long changed) {
        Request state(State next, String result, long tick) {
            return new Request(
                    id,
                    session,
                    next,
                    team,
                    dimension,
                    line,
                    x,
                    z,
                    endX,
                    endZ,
                    requester,
                    requesterName,
                    squad,
                    note,
                    result,
                    created,
                    expires,
                    tick);
        }

        public String title() {
            return "请求支援火力";
        }
    }

    @FunctionalInterface
    public interface Executor {
        boolean execute(Request request) throws Exception;
    }

    public static final class Denied extends RuntimeException {
        public Denied(String message) {
            super(message);
        }
    }

    private final Rules rules;
    private final LinkedHashMap<UUID, Request> requests = new LinkedHashMap<>();
    private final Map<UUID, Long> lastCreated = new HashMap<>();
    private long session, revision;

    public FireRequestBoard(Rules rules) {
        this.rules = rules;
    }

    public long session() {
        return session;
    }

    public long revision() {
        return revision;
    }

    public Request get(UUID id) {
        return requests.get(id);
    }

    public void reset(long nextSession) {
        session = nextSession;
        requests.clear();
        lastCreated.clear();
        revision++;
    }

    public List<Request> visible(Actor actor) {
        return actor.member()
                ? requests.values().stream().filter(r -> r.team().equals(actor.team())).toList()
                : List.of();
    }

    public Request create(
            Actor actor,
            String dimension,
            Bounds bounds,
            boolean line,
            double x,
            double z,
            double endX,
            double endZ,
            String note,
            long tick) {
        require(actor.requester(), "仅已部署的小队长或指挥官可申请火力");
        require(
                dimension != null
                        && !dimension.isBlank()
                        && bounds != null
                        && bounds.contains(x, z),
                "目标超出当前战场范围");
        if (line)
            require(
                    bounds.contains(endX, endZ)
                            && Math.hypot(endX - x, endZ - z) >= 1
                            && Math.hypot(endX - x, endZ - z) <= rules.maximumLineLength(),
                    "支援范围终点或长度超出允许范围");
        require(
                tick - lastCreated.getOrDefault(actor.id(), Long.MIN_VALUE / 2)
                        >= rules.createInterval(),
                "申请过于频繁，请稍后再试");
        require(
                requests.values().stream()
                                .filter(r -> r.team().equals(actor.team()) && r.state().active())
                                .count()
                        < rules.maximumPerTeam(),
                "本方待处理申请已达上限");
        while (requests.values().stream().filter(r -> r.team().equals(actor.team())).count()
                >= rules.maximumPerTeam()) {
            UUID oldest =
                    requests.values().stream()
                            .filter(r -> r.team().equals(actor.team()) && !r.state().active())
                            .map(Request::id)
                            .findFirst()
                            .orElseThrow(() -> new Denied("本方申请已达上限"));
            requests.remove(oldest);
        }
        Request request =
                new Request(
                        UUID.randomUUID(),
                        session,
                        State.PENDING,
                        actor.team(),
                        dimension,
                        line,
                        x,
                        z,
                        line ? endX : 0,
                        line ? endZ : 0,
                        actor.id(),
                        actor.name(),
                        actor.squad(),
                        clean(note),
                        "",
                        tick,
                        tick + rules.lifetime(),
                        tick);
        put(request);
        lastCreated.put(actor.id(), tick);
        return request;
    }

    private Request owned(UUID id, Actor actor, long tick) {
        Request r = requests.get(id);
        require(
                r != null
                        && actor.member()
                        && actor.team().equals(r.team())
                        && r.session() == session,
                "申请不存在或无权访问");
        if (r.state().active() && tick >= r.expires()) {
            put(r.state(State.EXPIRED, "申请已过期", tick));
            throw new Denied("申请已过期");
        }
        return r;
    }

    public void cancel(UUID id, Actor actor, long tick) {
        Request r = owned(id, actor, tick);
        require(
                r.state() == State.PENDING
                        && (actor.id().equals(r.requester()) || actor.commander()),
                "无权取消或申请已执行");
        put(r.state(State.CANCELLED, "申请已取消", tick));
    }

    public void reject(UUID id, Actor actor, long tick) {
        Request r = owned(id, actor, tick);
        require(
                actor.commander() && actor.deployed() && r.state() == State.PENDING,
                "只有已部署的指挥官可驳回待审批申请");
        put(r.state(State.REJECTED, "指挥官已驳回", tick));
    }

    public boolean approve(UUID id, Actor actor, long tick, Executor executor) {
        Request r = owned(id, actor, tick);
        require(
                actor.commander() && actor.deployed() && r.state() == State.PENDING,
                "只有已部署的指挥官可审批待执行申请");
        put(r.state(State.EXECUTING, "正在下达火力", tick));
        try {
            boolean success = executor.execute(r);
            put(
                    r.state(
                            success ? State.APPROVED : State.PENDING,
                            success ? "火力指令已下达" : "未执行，请检查支援冷却或目标",
                            tick));
            return success;
        } catch (Exception e) {
            // The callback may already have scheduled effects: never offer a blind retry.
            put(r.state(State.REJECTED, "调用结果异常，请管理员核查后重新申请", tick));
            throw new Denied("火力调用异常，已阻止该申请重复执行");
        }
    }

    public void maintain(long tick) {
        lastCreated.entrySet().removeIf(e -> tick - e.getValue() >= rules.createInterval());
        for (Request r : List.copyOf(requests.values()))
            if (r.state() == State.PENDING && tick >= r.expires())
                put(r.state(State.EXPIRED, "申请已过期", tick));
    }

    public void closeRound(long tick) {
        for (Request r : List.copyOf(requests.values()))
            if (r.state().active()) put(r.state(State.CANCELLED, "本局已结束", tick));
    }

    private void put(Request r) {
        requests.put(r.id(), r);
        revision++;
    }

    private static void require(boolean condition, String reason) {
        if (!condition) throw new Denied(reason);
    }

    public static String clean(String text) {
        String s = text == null ? "" : text.replaceAll("[\\p{Cntrl}§]", " ").strip();
        return s.length() > 96 ? s.substring(0, 96) : s;
    }
}
