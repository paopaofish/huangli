const lunisolar = require('lunisolar');

const d = lunisolar('2026-06-30 12:00:00');

console.log('Lunar year:', d.lunar.year);
console.log('Lunar month:', d.lunar.month);
console.log('Lunar day:', d.lunar.day);

console.log('getYearName():', d.lunar.getYearName());
console.log('getMonthName():', d.lunar.getMonthName());
console.log('getDayName():', d.lunar.getDayName());

// Let's print all properties (including symbols and non-enumerable ones) of d.lunar
console.log('lunar keys:', Object.keys(d.lunar));
console.log('lunar own property names:', Object.getOwnPropertyNames(d.lunar));

// Let's print out everything in d.lunar
for (let key in d.lunar) {
  if (typeof d.lunar[key] !== 'function') {
    console.log(`lunar.${key} =`, d.lunar[key]);
  }
}
