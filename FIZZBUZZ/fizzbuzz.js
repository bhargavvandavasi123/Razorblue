const newDiv = document.getElementById("content");
for (var i = 1; i <= 100; i++) {
  if (i % 15 == 0) {
    const newContent = document.createTextNode("FizzBuzz  \n\t  ");
    newDiv.appendChild(newContent);
  }
  if (i % 3 == 0) {
    const newContent = document.createTextNode('"Fizz"\n');
    newDiv.appendChild(newContent);
  }
  if (i % 5 == 0) {
    const newContent = document.createTextNode("Buzz\n  ");
    newDiv.appendChild(newContent);
  }
}
