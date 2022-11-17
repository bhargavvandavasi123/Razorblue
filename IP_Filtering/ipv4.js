// fetch("http://localhost:3000/ipv4")
//   .then((res) => res.json())
//   .then((data) => {
//     // if(given == data[0].ipv4.single){
//     //     console.log(ipv4 exists);
//     // }
//     // console.log(
//     //   ipv4Number(data[0].ipv4.range.split("-")[0]) <= ipv4Number(given) &&
//     //     ipv4Number(given) <= ipv4Number(data[0].ipv4.range.split("-")[1])
//     // );
//     // const cidr = parseCIDR(data[0].ipv4.cidr);
//     // console.log(
//     //   ipv4Number(cidr[0]) <= ipv4Number(given) &&
//     //     ipv4Number(given) <= ipv4Number(cidr[1])
//     // );
//   });

function ipv4() {
  var ipv4 = document.getElementById("ipv4").value;

  fetch("https://razorblue.azurewebsites.net/ipv4")
    .then((res) => res.json())
    .then((data) => {
      var cidr = parseCIDR(data[0].ipv4.cidr);

      if (
        ipv4 == data[0].ipv4.single ||
        (ipv4Number(data[0].ipv4.range.split("-")[0]) <= ipv4Number(ipv4) &&
          ipv4Number(ipv4) <= ipv4Number(data[0].ipv4.range.split("-")[1])) ||
        (ipv4Number(cidr[0]) <= ipv4Number(ipv4) &&
          ipv4Number(ipv4) <= ipv4Number(cidr[1]))
      ) {
        document.getElementById("output").innerHTML =
          "Supplied IPV4 Address exists in database";
      } else {
        document.getElementById("output").innerHTML =
          "Supplied IPV4 Address doesnot exist in the database, Please try a new IPV4 address";
      }
    });
}

var parseCIDR = function (CIDR) {
  //Beginning IP address
  var beg = CIDR.substr(CIDR, CIDR.indexOf("/"));
  var end = beg;
  var off = (1 << (32 - parseInt(CIDR.substr(CIDR.indexOf("/") + 1)))) - 1;
  var sub = beg.split(".").map(function (a) {
    return parseInt(a);
  });

  //An IPv4 address is just an UInt32...
  var buf = new ArrayBuffer(4); //4 octets
  var i32 = new Uint32Array(buf);

  //Get the UInt32, and add the bit difference
  i32[0] = (sub[0] << 24) + (sub[1] << 16) + (sub[2] << 8) + sub[3] + off;

  //Recombine into an IPv4 string:
  var end = Array.apply([], new Uint8Array(buf)).reverse().join(".");

  return [beg, end];
};

//console.log(parseCIDR("192.168.1.0/24"));

const ipv4Number = (ip) => {
  iparray = ip.split(".");
  ipnumber =
    parseInt(iparray[3]) +
    parseInt(iparray[2]) * 256 +
    parseInt(iparray[1]) * Math.pow(256, 2) +
    parseInt(iparray[0]) * Math.pow(256, 3);
  return ipnumber;
};

console.log(ipv4Number("192.168.1.10"));
