const lunisolar = require('lunisolar');

const d = lunisolar('2026-06-30 12:00:00');

// Let's test a bunch of format strings
const formats = [
  'YYYY-MM-DD HH:mm:ss',
  'Y M D h',
  'lY lM lD lH',
  'cY cM cD cH',
  'iY iM iD iH',
  'GD GM GD GH',
  'z', 'Z', 's', 'S',
  'lYn lMn lDn',
  'cYear cMonth cDay cHour'
];

formats.forEach(f => {
  console.log(`d.format('${f}') =`, d.format(f));
});

// Let's inspect the locales
const zhLocale = lunisolar.getLocale('zh') || lunisolar.getLocale() || lunisolar._globalConfig.locales.zh;
console.log('ZH Locale properties:', Object.keys(zhLocale));
console.log('chineseZodiac:', zhLocale.chineseZodiac);
console.log('lunarMonths:', zhLocale.lunarMonths);
console.log('lunarDays:', zhLocale.lunarDays);
console.log('stems:', zhLocale.stems);
console.log('branchs:', zhLocale.branchs);
