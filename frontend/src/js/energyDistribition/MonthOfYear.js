class MonthOfYear {
    constructor(month, year) {
        this.month = month
        this.year = year
    }
}

MonthOfYear.fromJson = function (jsonObj) {
    if (jsonObj == null) {
        return null
    }
    if (jsonObj instanceof Array) {
        return jsonObj.map(MonthOfYear.fromJson)
    }
    if (jsonObj instanceof Object) {
        return new MonthOfYear(jsonObj.month, jsonObj.year)
    }
    throw Error("Unexpected jsonObject")
}


export default MonthOfYear