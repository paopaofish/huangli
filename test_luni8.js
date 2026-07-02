const lunisolar = require('lunisolar');

const d = lunisolar('2026-06-10 12:00:00');
console.log('On 2026-06-10 12:00:00:');
console.log('Lunar Month:', d.lunar.month);
console.log('Lunar Day:', d.lunar.day);
console.log('Char8 month:', d.char8.month.toString());
