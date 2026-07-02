const lunisolar = require('lunisolar');

// In 2026:
// Lunar New Year (正月初一) is 2026-02-17
// Li Chun (立春) is 2026-02-04 (around 16:30)

console.log('--- 2026-02-03 (Before Li Chun, Before Lunar New Year) ---');
const d_feb03 = lunisolar('2026-02-03 12:00:00');
console.log('Lunar:', d_feb03.lunar.year, d_feb03.lunar.month, d_feb03.lunar.day);
console.log('Char8 year:', d_feb03.char8.year.toString());
console.log('Zodiac:', d_feb03.lunar.zodiac);

console.log('--- 2026-02-05 (After Li Chun, Before Lunar New Year) ---');
const d_feb05 = lunisolar('2026-02-05 12:00:00');
console.log('Lunar:', d_feb05.lunar.year, d_feb05.lunar.month, d_feb05.lunar.day);
console.log('Char8 year:', d_feb05.char8.year.toString());
console.log('Zodiac:', d_feb05.lunar.zodiac);

console.log('--- 2026-02-18 (After Li Chun, After Lunar New Year) ---');
const d_feb18 = lunisolar('2026-02-18 12:00:00');
console.log('Lunar:', d_feb18.lunar.year, d_feb18.lunar.month, d_feb18.lunar.day);
console.log('Char8 year:', d_feb18.char8.year.toString());
console.log('Zodiac:', d_feb18.lunar.zodiac);

console.log('\n--- Checking changeAgeTerm option ---');
// Let's see if changeAgeTerm can be changed
[1, 2, 3].forEach(term => {
  const customLuni = lunisolar.config({ changeAgeTerm: term });
  const d = lunisolar('2026-02-05 12:00:00'); // After Li Chun, Before Lunar New Year
  console.log(`changeAgeTerm: ${term} -> Char8 year on 2026-02-05:`, d.char8.year.toString(), 'Zodiac:', d.lunar.zodiac);
});
