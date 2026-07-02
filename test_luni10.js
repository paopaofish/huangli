const lunisolar = require('lunisolar');
const theGods = require('lunisolar/plugins/theGods');

// Extend lunisolar with theGods plugin
lunisolar.extend(theGods);

const d = lunisolar('2026-06-30 12:00:00');

console.log('--- Daily Yiji (宜忌) on 2026-06-30 ---');
console.log('Suitable (宜):', d.theGods.good);
console.log('Taboo (忌):', d.theGods.bad);

// Let's print out what methods are on d.theGods
console.log('theGods keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(d.theGods)));
