const lunisolar = require('lunisolar');
const theGods = require('lunisolar/plugins/theGods');
lunisolar.extend(theGods);

const dateStr = '2001-05-25 12:00:00';
try {
  const d = lunisolar(dateStr);
  console.log('lunar:', d.lunar.toString());
  console.log('char8:', d.char8.toString());
  console.log('duty:', d.theGods.getDuty12God().toString());
  console.log('goodActs:', d.theGods.getGoodActs());
} catch (err) {
  console.error('Error:', err.message);
  console.error(err.stack);
}
