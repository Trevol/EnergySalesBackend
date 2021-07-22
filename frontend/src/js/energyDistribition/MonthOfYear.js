class MonthOfYear {
    constructor(month, year) {
        this.month = month
        this.year = year
        this.display = `${this._monthName()} ${this.year}`
    }

    _monthName() {
        return months[this.month - 1]
    }

    subtractMonth() {
        if (this.month > 1) {
            return new MonthOfYear(this.month - 1, this.year)
        }
        //month == 1 => 12 month of prev year
        return new MonthOfYear(12, this.year - 1)
    }

    greaterThan(other) {
        if (!(other instanceof MonthOfYear)) {
            throw new Error("other is not instanceof MonthOfYear")
        }
        return (this.year > other.year) || (this.year === other.year && this.month > other.month)
    }

    greaterThanOrEqual(other) {
        return this.greaterThan(other) || this.equals(other)
    }

    equals(other) {
        return other instanceof MonthOfYear && this.year === other.year && this.month === other.month
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