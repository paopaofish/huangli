const lunisolar = require('lunisolar');

const d = lunisolar('2026-06-30 12:00:00');

const sbYear = d.char8.year;
console.log('SB year prototype keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(sbYear)));
console.log('SB year own properties:', Object.getOwnPropertyNames(sbYear));

console.log('SB year stem:', sbYear.stem.toString(), 'stem keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(sbYear.stem)));
console.log('SB year branch:', sbYear.branch.toString(), 'branch keys:', Object.getOwnPropertyNames(Object.getPrototypeOf(sbYear.branch)));

// Let's print everything in sbYear.branch
const branch = sbYear.branch;
for (let key in branch) {
  if (typeof branch[key] !== 'function') {
    console.log(`branch.${key} =`, branch[key]);
  }
}
// Is there a zodiac? Let's check the getters/methods of branch
const branchMethods = Object.getOwnPropertyNames(Object.getPrototypeOf(branch));
console.log('Branch methods:', branchMethods);

// Let's try to call some methods on branch
branchMethods.forEach(method => {
  try {
    if (typeof branch[method] === 'function') {
      console.log(`branch.${method}() =`, branch[method]());
    } else {
      console.log(`branch.${method} =`, branch[method]);
    }
  } catch (e) {
    // console.log(`Error calling branch.${method}:`, e.message);
  }
});
