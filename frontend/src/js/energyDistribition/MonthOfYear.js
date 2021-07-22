class MonthOfYear {
    constructor(month, year) {
        this.month = month
        this.year = year
        this.display = `${this._monthName()} ${this.year}`
    }

    _monthName() {
        return months[this.month - 1]
    }
}

const months = [
    "Январь",
    "Февраль",
    "Март",
    "Апрель",
    "Май",
    "Июнь",
    "Июль",
    "Август",
    "Сентябрь",
    "Октябрь",
    "Ноябрь",
    "Декабрь",
]

MonthOfYear.toInstance = function (obj) {
    if (obj instanceof MonthOfYear) {
        return obj
    }
    return MonthOfYear.fromPlainObject(obj)
}

MonthOfYear.fromPlainObject = function (obj) {
    if (obj == null) {
        return null
    }
    if (obj instanceof Object) {
        return new MonthOfYear(obj.month, obj.year)
    }
    throw Error("Unexpected obj " + obj)
}

MonthOfYear.fromPlainArray = function (array) {
    if (array instanceof Array) {
        return array.map(MonthOfYear.fromPlainObject)
    }
    throw Error("Unexpected array " + array)
}


export default MonthOfYear