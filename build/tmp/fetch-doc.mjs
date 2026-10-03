import { createRequire } from "module";
const require = createRequire("C:/Users/Administrator/.dsh/profiles/node_modules/");
const https = require("https");
function get(url, retries) {
  return new Promise((resolve) => {
    const doReq = (n) => {
      const req = https.get(url, { rejectUnauthorized: false, headers: { "User-Agent": "Mozilla/5.0" } }, (res) => {
        let d = "";
        res.on("data", (c) => (d += c));
        res.on("end", () => resolve({ status: res.statusCode, body: d }));
      });
      req.on("error", (e) => {
        if (n > 0) { setTimeout(() => doReq(n - 1), 2000); } else { resolve({ status: 0, body: "ERR " + e.message }); }
      });
    };
    doReq(retries);
  });
}
const r = await get("https://raw.githubusercontent.com/MCSManager/MCSManager/master/panel/src/app/routers/filemananger_router.ts", 4);
const i = r.body.indexOf("router.delete");
console.log(r.body.slice(i - 200, i + 1500));