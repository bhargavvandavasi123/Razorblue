function importing() {
  var onload = fetch("./Technical_Test_Data.csv")
    .then((res) => {
      return res.text();
    })
    .then((data) => {
      const temp = data.split(/\r?\r/);
      //console.log(temp);
      const result = temp.map((val) => val.split(","));
      const petrolCars = [];
      const DieselCars = [];
      const HybridCars = [];
      const ElectricCars = [];
      var cars = new Map();
      var final = [];
      for (var i = 1; i < result.length - 1; i++) {
        if (!cars.has(result[i][0])) {
          cars.set(result[i][0], [result[(i, i)]]);
          final.push(result[(i, i)]);
        }
      }
      // console.log(final);

      for (var i = 0; i < final.length; i++) {
        if (final[i][4] == "Petrol") {
          petrolCars.push(final[i]);
        } else if (final[i][4] == "Diesel") {
          DieselCars.push(final[i]);
        } else if (final[i][4] == "Electric") {
          ElectricCars.push(final[i]);
        } else if (final[i][4] == "Hybrid") {
          HybridCars.push(final[i]);
        }
      }

      //   const fuelTypes = (filename, fueltype) => {
      //     let filename = filename;
      //     let text = fueltype.map((res) => res.join(","));
      //     let blob = new Blob([text], { type: "text/plain" });
      //     let link = document.createElement("a");
      //     link.download = filename;
      //     link.href = window.URL.createObjectURL(blob);
      //     document.body.appendChild(link);
      //     link.click();
      //     setTimeout(() => {
      //       document.body.removeChild(link);
      //       window.URL.revokeObjectURL(link.href);
      //     }, 100);
      //   };

      fuelTypes("petrolCars.csv", petrolCars);
      fuelTypes("dieselCars.csv", DieselCars);
      fuelTypes("electricCars.csv", ElectricCars);
      fuelTypes("HybridCars.csv", HybridCars);

      //   let text = petrolCars.map((res) => res.join(","));
      //   let blob = new Blob([text], { type: "text/plain" });
      //   let link = document.createElement("a");
      //   link.download = "petrol.csv";
      //   link.href = window.URL.createObjectURL(blob);
      //   document.body.appendChild(link);
      //   link.click();

      let validRegistrations = [];
      let inValidRegistrations = 0;
      final.forEach((entry) => {
        // console.log(entry[0]);
        let result = entry[0].match(/\n[A-Z][A-Z][0-9][0-9] [A-Z ]{3}/gi);
        if (result != null) {
          validRegistrations.push(entry);
        } else {
          inValidRegistrations += 1;
        }

        //console.log(validRegistrations);
        // let regexp = /\n[a-zA-Z]{2}[0-9][0-9] [a-zA-Z]{3}+/;
        // let result = entry[0].match(regexp);
        //console.log(result);
      });
      console.log(validRegistrations);
      fuelTypes("validRegistrations.csv", validRegistrations);
      document.getElementById("validRegistrations").innerHTML =
        validRegistrations.length;
      document.getElementById("invalidRegistrations").innerHTML =
        inValidRegistrations;
    });
}

function fuelTypes(filename, fuelType) {
  let text = fuelType.map((res) => res.join(","));
  let blob = new Blob([text], { type: "text/plain" });
  let link = document.createElement("a");
  link.download = filename;
  link.href = window.URL.createObjectURL(blob);
  document.body.appendChild(link);
  link.click();
}
