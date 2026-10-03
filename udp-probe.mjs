import dgram from 'node:dgram';
const host = '180.165.15.38';
function probe(port) {
  return new Promise(resolve => {
    const s = dgram.createSocket('udp4');
    let done = false;
    const finish = (r) => { if (!done) { done = true; try { s.close(); } catch {} resolve(`UDP ${port}: ${r}`); } };
    s.on('error', e => finish('error ' + e.code));
    s.on('message', () => finish('有回应(端口开着)'));
    s.connect(port, host, () => {
      s.send(Buffer.from([0x00]), err => { if (err) finish('send error ' + err.code); });
    });
    setTimeout(() => finish('超时/无回应（可能开放被忽略，或被防火墙丢弃）'), 2500);
  });
}
for (const p of [24454, 23384, 59999]) console.log(await probe(p));