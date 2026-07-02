const lunisolar = require('lunisolar');

console.log('Lunisolar exports:', Object.keys(lunisolar));
console.log('Lunisolar function string:', lunisolar.toString());

// Let's print out what methods/properties are on a lunisolar instance
const d = lunisolar();
console.log('Lunisolar instance prototype keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(d)));

// Check if there are any config or settings
console.log('Lunisolar config:', lunisolar.config);

// Let's create a date and print its lunar and char8 data
const d2 = lunisolar('2026-06-30 23:18:17');
console.log('Solar Date: 2026-06-30 23:18:17');
console.log('Lunar Year:', d2.lunar.year);
console.log('Lunar Month:', d2.lunar.month);
console.log('Lunar Day:', d2.lunar.day);
console.log('Lunar MonthName:', d2.lunar.monthStr);
console.log('Lunar DayName:', d2.lunar.dayStr);

// Bazi (Char8) data
const char8 = d2.char8;
console.log('Char8 prototype keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(char8)));
console.log('Char8 year:', char8.year.toString());
console.log('Char8 month:', char8.month.toString());
console.log('Char8 day:', char8.day.toString());
console.log('Char8 hour:', char8.hour.toString());
console.log('Zodiac:', d2.lunar.zodiac);

// Check if there is an isLeap or similar
console.log('Is leap month:', d2.lunar.isLeap);

// Check Yiji (宜忌)
console.log('Lunisolar instance keys:', Object.keys(d2));
// Let's see if we can inspect how to get Yiji. Usually there is a plugin or built-in.
// Let's print out the list of methods to see if we can find any method like "suitable" or "taboo" or "yiji"
const methods = Object.getOwnPropertyNames(Object.getPrototypeOf(d2));
console.log('Methods on d2:', methods);
