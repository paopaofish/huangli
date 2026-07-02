const lunisolar = require('lunisolar');

const d1 = lunisolar('2026-06-30 12:00:00');
const d2 = lunisolar('2026-06-30 23:18:17');

console.log('--- Date comparison for June 30, 2026 ---');
console.log('At 12:00:00:');
console.log('  Lunar Year/Month/Day:', d1.lunar.year, d1.lunar.month, d1.lunar.day);
console.log('  Char8 Year/Month/Day/Hour:', d1.char8.year.toString(), d1.char8.month.toString(), d1.char8.day.toString(), d1.char8.hour.toString());

console.log('At 23:18:17:');
console.log('  Lunar Year/Month/Day:', d2.lunar.year, d2.lunar.month, d2.lunar.day);
console.log('  Char8 Year/Month/Day/Hour:', d2.char8.year.toString(), d2.char8.month.toString(), d2.char8.day.toString(), d2.char8.hour.toString());

// Lunar properties
console.log('Lunar prototype keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(d1.lunar)));

// Global Config
console.log('Global Config:', lunisolar._globalConfig);

// Let's see if we can find options in _globalConfig
console.log('Global Config keys:', Object.keys(lunisolar._globalConfig));
