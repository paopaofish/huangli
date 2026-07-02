const lunisolar = require('lunisolar');
const d = lunisolar('2023-04-15 12:00:00'); // 2023 is Year of Rabbit (兔)

function scan(obj, path = 'd', visited = new Set()) {
  if (obj === null || obj === undefined || visited.has(obj)) return;
  visited.add(obj);

  if (typeof obj === 'string' && obj.includes('兔')) {
    console.log(`Found string '兔' at path: ${path} -> "${obj}"`);
  }

  try {
    for (const key of Object.getOwnPropertyNames(obj)) {
      try {
        const val = obj[key];
        scan(val, `${path}.${key}`, visited);
      } catch (e) {}
    }
    const proto = Object.getPrototypeOf(obj);
    if (proto) {
      for (const key of Object.getOwnPropertyNames(proto)) {
        try {
          const val = typeof obj[key] === 'function' ? obj[key]() : obj[key];
          scan(val, `${path}.${key}()`, visited);
        } catch (e) {}
      }
    }
  } catch (e) {}
}

scan(d);
