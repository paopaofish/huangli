const lunisolar = require('lunisolar');

console.log('On 2026-06-30 00:30:00 (Early Zi Shi):');
const d1 = lunisolar('2026-06-30 00:30:00');
console.log('  Char8 Day/Hour:', d1.char8.day.toString(), d1.char8.hour.toString());

console.log('On 2026-06-30 12:00:00 (Wu Shi):');
const d2 = lunisolar('2026-06-30 12:00:00');
console.log('  Char8 Day/Hour:', d2.char8.day.toString(), d2.char8.hour.toString());

console.log('On 2026-06-30 23:30:00 (Late Zi Shi):');
const d3 = lunisolar('2026-06-30 23:30:00');
console.log('  Char8 Day/Hour:', d3.char8.day.toString(), d3.char8.hour.toString());
