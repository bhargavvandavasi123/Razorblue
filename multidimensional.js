const multi = [
  [1, 2, 3, 4],
  [5, 6, 7, 8],
  [9, 10, 11, 12],
  [13, 14, 15, 16],
];

let sum = 0;

multi.map((val, index, arr) => {
  sum += val[index] + val[arr.length - 1 - index];
});

console.log(sum);
