import MonthOfYear from "./MonthOfYear";

test("Simple creation test", () => {
    const m = new MonthOfYear(10, 2020)
    expect(m).toBeInstanceOf(MonthOfYear)
    expect(m.month).toBe(10)
    expect(m.year).toBe(2020)
    expect(m.display).toBe("Октябрь 2020")
})

test("toInstance test", () => {
    expect(MonthOfYear.toInstance(null)).toBeNull()

    let monthOfYear = new MonthOfYear(5, 2020);
    expect(MonthOfYear.toInstance(monthOfYear) === monthOfYear).toBe(true)

    expect(MonthOfYear.toInstance({month: 5, year: 2020})).toStrictEqual(new MonthOfYear(5, 2020))
})

test("MonthOfYear.fromPlainObject", () => {
    expect(MonthOfYear.fromPlainObject(null)).toBeNull()
    expect(MonthOfYear.fromPlainObject(undefined)).toBeNull()
    const plainObj = {
        month: 10,
        year: 2020
    }
    expect(MonthOfYear.fromPlainObject(plainObj)).toStrictEqual(new MonthOfYear(10, 2020))
})

test("MonthOfYear.fromPlainArray", () => {
    expect(MonthOfYear.fromPlainArray([null])).toStrictEqual([null])

    const plainArray = [
        {
            month: 10,
            year: 2020
        },
        null,
        {
            month: 1,
            year: 2019
        }
    ]

    expect(MonthOfYear.fromPlainArray(plainArray))
        .toStrictEqual([
            new MonthOfYear(10, 2020), null, new MonthOfYear(1, 2019)
        ])

})

test("subtractMonth", () => {
    expect(new MonthOfYear(12, 2020).subtractMonth()).toStrictEqual(new MonthOfYear(11, 2020))
    expect(new MonthOfYear(1, 2020).subtractMonth()).toStrictEqual(new MonthOfYear(12, 2019))
})

test("greaterThan", () => {
    expect(
        new MonthOfYear(12, 2020).greaterThan(new MonthOfYear(12, 2020))
    ).toBe(false)
    expect(
        new MonthOfYear(12, 2020).greaterThan(new MonthOfYear(11, 2020))
    ).toBe(true)
    expect(
        new MonthOfYear(11, 2020).greaterThan(new MonthOfYear(12, 2020))
    ).toBe(false)
})

test("greaterThanOrEqual", () => {
    expect(
        new MonthOfYear(12, 2020).greaterThanOrEqual(new MonthOfYear(12, 2020))
    ).toBe(true)
    expect(
        new MonthOfYear(12, 2020).greaterThanOrEqual(new MonthOfYear(11, 2020))
    ).toBe(true)
    expect(
        new MonthOfYear(11, 2020).greaterThanOrEqual(new MonthOfYear(12, 2020))
    ).toBe(false)
})

test("equals", () => {
    expect(
        new MonthOfYear(12, 2020).equals(new MonthOfYear(12, 2020))
    ).toBe(true)
    expect(
        new MonthOfYear(12, 2020).equals({month: 12, year: 2020})
    ).toBe(false)
    expect(
        new MonthOfYear(12, 2020).equals(new MonthOfYear(11, 2020))
    ).toBe(false)
})