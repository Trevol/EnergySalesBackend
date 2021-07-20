import _ from "lodash"


class MonthOfYear {
    constructor(month, year) {
        this.month = month
        this.year = year
    }

    doIt() {
        console.log("doIt", this.month, this.year)
    }
}

let monthOfYear = new MonthOfYear(5, 2010);
let json = JSON.stringify(monthOfYear);

let m = new MonthOfYear;
let source = JSON.parse(json);
source.ddd = "ttt"
let d = Object.assign(m, source)
d.doIt()
console.log(_(d).keys().value())
