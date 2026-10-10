// MCSM 面板部署助手：登录（cookie+token）→ REST 调用 → socket.io 控制台。
// 用法: node mcsm-deploy.js <action> [args...]
// actions:
//   list [path]                 列目录
//   move <from> <to>            重命名
//   delete <path>...            删除
//   fetch <url> <file_name>     服务端从 URL 下载到实例内路径
//   restart                     重启实例（无控制台跟随）
//   console <cmd> [cmd...]      经 stream 发送控制台命令并回显输出
//   tail <seconds>              打开 stream 输出 N 秒（观察启动日志）
const WebSocket = require("C:/Users/Administrator/.dsh/profiles/node_modules/ws");

const PANEL = "https://www.derpydoge.fun:20000";
const USER = "boy";
const PASS = "boY1145141919810Fuck";
const DAEMON = "647a21e800714185aec74849f1b0f9a6";
const INSTANCE = "cd06335bc1534e028d92b4b335be248a";

const [action, ...args] = process.argv.slice(2);

async function login() {
  const res = await fetch(`${PANEL}/api/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json", "X-Requested-With": "XMLHttpRequest" },
    body: JSON.stringify({ username: USER, password: PASS }),
  });
  const body = await res.json();
  if (body.status !== 200) throw new Error("login failed: " + JSON.stringify(body));
  const cookies = (res.headers.getSetCookie?.() ?? [])
    .map((line) => line.split(";")[0]).join("; ");
  return { token: body.data, cookie: cookies };
}

async function api(session, method, path, query, data) {
  const q = new URLSearchParams({ daemonId: DAEMON, uuid: INSTANCE, token: session.token, ...query });
  const headers = { "X-Requested-With": "XMLHttpRequest" };
  if (session.cookie) headers.Cookie = session.cookie;
  const init = { method, headers };
  if (data !== undefined) {
    init.body = JSON.stringify(data);
    headers["Content-Type"] = "application/json";
  }
  const res = await fetch(`${PANEL}${path}?${q}`, init);
  const text = await res.text();
  let json;
  try { json = JSON.parse(text); } catch { json = text; }
  if (res.status !== 200 || (json && json.status && json.status !== 200)) {
    throw new Error(`${method} ${path} -> ${res.status}: ${typeof json === "string" ? json : JSON.stringify(json)}`);
  }
  return json;
}

function wsUrl(addr, prefix) {
  const base = addr.replace(/^http:/, "ws:").replace(/\/+$/, "");
  const path = (prefix ? (prefix.startsWith("/") ? prefix : `/${prefix}`) : "") + "/socket.io/";
  return `${base}${path}?EIO=4&transport=websocket`;
}

async function streamChannel(session) {
  const channel = await api(session, "POST", "/api/protected_instance/stream_channel", {}, undefined);
  const payload = channel?.data ?? channel;
  let addr = String(payload.addr ?? "");
  const password = String(payload.password ?? "");
  const prefix = payload.prefix;
  if (!addr) throw new Error("stream addr missing");
  if (/localhost|127\.0\.0\.1/.test(addr)) {
    const host = PANEL.replace(/^https?:\/\//, "").split("/")[0].split(":")[0];
    const port = addr.match(/:(\d+)/)?.[1];
    addr = addr.replace(/^wss?:\/\/[^/]+/, "wss://" + host + (port ? ":" + port : ""));
  }
  return { addr, password, prefix };
}

function openStream(session, { addr, password, prefix }) {
  return new Promise((resolve, reject) => {
    const socket = new WebSocket(wsUrl(addr, prefix));
    const buffer = [];
    let authed = false;
    let done = false;
    const finish = (err) => {
      if (done) return;
      done = true;
      try { socket.close(); } catch {}
      if (err) reject(err); else resolve(buffer.join(""));
    };
    socket.on("error", (e) => finish(e));
    socket.on("close", (code) => { if (!done) finish(new Error("stream closed " + code)); });
    socket.on("message", (raw) => {
      const text = raw.toString();
      for (const frame of text.split("\x1e")) {
        if (frame === "") continue;
        const type = frame[0];
        const data = frame.slice(1);
        if (type === "0") socket.send("40");
        else if (type === "2") socket.send("3");
        else if (type === "4") {
          const sioType = data[0];
          const payload = data.slice(1);
          if (sioType === "0") {
            socket.send(`42${JSON.stringify(["stream/auth", { data: { password } }])}`);
          } else if (sioType === "2") {
            let event;
            try { event = JSON.parse(payload); } catch { return; }
            const name = event[0];
            const arg = event[1];
            if (name === "stream/auth") {
              if (arg?.data !== true) return finish(new Error("stream auth failed"));
              authed = true;
              socket.send(`42${JSON.stringify(["stream/detail", {}])}`);
            } else if (name === "instance/stdout") {
              const out = arg?.data;
              const textOut = typeof out === "string" ? out : out?.text ?? "";
              if (textOut) buffer.push(textOut);
            }
          }
        }
      }
    });
    socket.on("open", () => {});
    global.__finishStream = finish;
    global.__socket = socket;
  });
}

(async () => {
  const session = await login();
  switch (action) {
    case "list": {
      const path = args[0] || ".";
      const data = await api(session, "GET", "/api/files/list", { page: 0, page_size: 500, file_name: "", target: path });
      const items = Array.isArray(data.items) ? data.items : data.data?.items ?? [];
      console.log((data.absolutePath ?? data.data?.absolutePath ?? path));
      for (const it of items) console.log(`${it.type === 0 ? "DIR " : "FILE"} ${it.size} ${it.name}`);
      break;
    }
    case "move": {
      const [from, to] = args;
      await api(session, "PUT", "/api/files/move", {}, { targets: [[from, to]] });
      console.log(`moved ${from} -> ${to}`);
      break;
    }
    case "delete": {
      await api(session, "DELETE", "/api/files", {}, { targets: args });
      console.log(`deleted: ${args.join(", ")}`);
      break;
    }
    case "fetch": {
      const [url, file_name] = args;
      const r = await api(session, "POST", "/api/files/download_from_url", {}, { url, file_name });
      console.log("download task started:", JSON.stringify(r).slice(0, 300));
      break;
    }
    case "restart": {
      const r = await api(session, "GET", "/api/protected_instance/restart", {}, undefined);
      console.log("restart:", JSON.stringify(r).slice(0, 200));
      break;
    }
    case "console": {
      const ch = await streamChannel(session);
      const socket = new WebSocket(wsUrl(ch.addr, ch.prefix));
      let authed = false;
      let out = "";
      let settled = false;
      const settle = () => {
        if (settled) return;
        settled = true;
        try { socket.close(); } catch {}
        console.log(out.split(/\r?\n/).filter((l) => l.trim()).slice(-200).join("\n"));
        process.exit(0);
      };
      socket.on("error", (e) => { console.error("stream error", e.message); process.exit(1); });
      socket.on("close", () => { if (!settled) { console.log(out.slice(-2000)); process.exit(0); } });
      socket.on("message", (raw) => {
        const text = raw.toString();
        for (const frame of text.split("\x1e")) {
          if (frame === "") continue;
          const type = frame[0];
          const data = frame.slice(1);
          if (type === "0") socket.send("40");
          else if (type === "2") socket.send("3");
          else if (type === "4") {
            const sioType = data[0];
            const payload = data.slice(1);
            if (sioType === "0") socket.send(`42${JSON.stringify(["stream/auth", { data: { password: ch.password } }])}`);
            else if (sioType === "2") {
              let event;
              try { event = JSON.parse(payload); } catch { return; }
              if (event[0] === "stream/auth") {
                if (event[1]?.data !== true) { console.error("auth failed"); process.exit(1); }
                authed = true;
                socket.send(`42${JSON.stringify(["stream/detail", {}])}`);
                let i = 0;
                const timer = setInterval(() => {
                  socket.send(`42${JSON.stringify(["stream/input", { data: { command: args[i++] } }])}`);
                  if (i >= args.length) { clearInterval(timer); setTimeout(settle, 2500); }
                }, 800);
              } else if (event[0] === "instance/stdout") {
                const o = event[1]?.data;
                const t = typeof o === "string" ? o : o?.text ?? "";
                if (t) out += t;
              }
            }
          }
        }
      });
      break;
    }
    case "tail": {
      const seconds = parseInt(args[0] || "20", 10);
      const ch = await streamChannel(session);
      const socket = new WebSocket(wsUrl(ch.addr, ch.prefix));
      let out = "";
      socket.on("error", (e) => { console.error("stream error", e.message); process.exit(1); });
      socket.on("message", (raw) => {
        const text = raw.toString();
        for (const frame of text.split("\x1e")) {
          if (frame === "") continue;
          const type = frame[0];
          const data = frame.slice(1);
          if (type === "0") socket.send("40");
          else if (type === "2") socket.send("3");
          else if (type === "4") {
            const sioType = data[0];
            const payload = data.slice(1);
            if (sioType === "0") socket.send(`42${JSON.stringify(["stream/auth", { data: { password: ch.password } }])}`);
            else if (sioType === "2") {
              let event;
              try { event = JSON.parse(payload); } catch { return; }
              if (event[0] === "instance/stdout") {
                const o = event[1]?.data;
                const t = typeof o === "string" ? o : o?.text ?? "";
                if (t) { process.stdout.write(t); out += t; }
              }
            }
          }
        }
      });
      setTimeout(() => { try { socket.close(); } catch {} process.exit(0); }, seconds * 1000);
      break;
    }
    default:
      console.error("unknown action", action);
      process.exit(1);
  }
})().catch((e) => { console.error("ERROR:", e.message); process.exit(1); });
