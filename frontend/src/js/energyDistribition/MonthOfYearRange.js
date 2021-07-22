import MonthOfYear from "./MonthOfYear";

class MonthOfYearRange {
    constructor(start, end, lastWithReadings) {
        this.start = MonthOfYear.toInstance(start)
        this.end = MonthOfYear.toInstance(end)
        this.lastWithReadings = MonthOfYear.toInstance(lastWithReadings)
    }

    toMonthsList() {
        throw "Not implemented"
    }
}

MonthOfYearRange.toInstance = function (obj) {
    if (obj instanceof MonthOfYearRange) {
        return obj
    }
    return MonthOfYearRange.fromPlainObject(obj)
}

MonthOfYearRange.fromPlainObject = function (obj) {
    if (obj == null) {
        return null
    }
    if (obj instanceof Object) {
        return new MonthOfYearRange(
            obj.start,
            obj.end,
            obj.lastWithReadings
        )
    }
    throw Error("Unexpected obj " + obj)
}

export default MonthOfYearRange