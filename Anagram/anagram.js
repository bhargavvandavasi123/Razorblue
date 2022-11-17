function anagram() {
  var firstString = document.getElementById("firstString").value;
  var secondString = document.getElementById("secondString").value;

  if (firstString == "" && secondString == "") {
    document.getElementById("output").innerHTML = "Please enter the strings";
  } else {
    const value =
      firstString.split("").sort().join("") ==
      secondString.split("").sort().join("");

    if (value) {
      document.getElementById("output").innerHTML =
        "Given Strings are ANAGRAMS";
    }
    if (!value) {
      document.getElementById("output").innerHTML =
        "Given strings are NOT ANAGRAMS";
    }
  }
}
