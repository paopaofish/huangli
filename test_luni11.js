try {
  const lunisolar = require('lunisolar');
  const theGods = require('lunisolar/plugins/theGods');

  lunisolar.extend(theGods);

  const d = lunisolar('2026-06-30 12:00:00');

  console.log('--- Daily Yiji (宜忌) on 2026-06-30 ---');
  console.log('getGoodActs():', d.theGods.getGoodActs());
  console.log('getBadActs():', d.theGods.getBadActs());
  console.log('getDuty12God():', d.theGods.getDuty12God().toString());
  console.log('getBy12God():', d.theGods.getBy12God().toString());
} catch (err) {
  console.error('Error occurred:', err.message);
  console.error(err.stack);
}
