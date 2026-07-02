const lunisolar = require('lunisolar');
lunisolar.locale('zh-cn'); // Switch to zh-cn

const d = lunisolar('2023-04-15 12:00:00');
console.log('Zodiac simplified:', d.format('cZ'));
console.log('Lunar month name simplified:', d.lunar.getMonthName());
