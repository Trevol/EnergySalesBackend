import MonthOfYear from "./MonthOfYear";

class MonthOfYearRange {
    constructor(start, end, lastWithReadings) {
        this.start = MonthOfYear.toMonthOfYear(start)
        this.end = MonthOfYear.toMonthOfYear(end)
        this.lastWithReadings = MonthOfYear.toMonthOfYear(lastWithReadings)
    }

    toMonthsList() {
        throw "Not implemented"
    }
}

MonthOfYearRange.fromJson = function (jsonObj) {
    if (jsonObj == null) {
        return null
    }
    if (jsonObj instanceof Array) {
        return jsonObj.map(MonthOfYearRange.fromJson)
    }
    if (jsonObj instanceof Object) {
        return new MonthOfYearRange(
            MonthOfYear.fromJson(jsonObj.start),
            MonthOfYear.fromJson(jsonObj.end),
            MonthOfYear.fromJson(jsonObj.lastWithReadings)
        )
    }
    throw Error("Unexpected jsonObject")
}

export default MonthOfYearRange