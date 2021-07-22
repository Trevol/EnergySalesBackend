import MonthOfYear from "./MonthOfYear";

test("Simple creation test", () => {
    const m = new MonthOfYear(10, 2020)
    expect(m).toBeInstanceOf(MonthOfYear)
    expect(m.month).toBe(10)
    expect(m.year).toBe(2020)
})

test("toMonthOfYear test", () => {
    expect(MonthOfYear.toMonthOfYear(null)).toBeNull()

    let monthOfYear = new MonthOfYear(5, 2020);
    expect(MonthOfYear.toMonthOfYear(monthOfYear) === monthOfYear).toBe(true)

    expect(MonthOfYear.toMonthOfYear({month: 5, year: 2020})).toStrictEqual(new MonthOfYear(5, 2020))
})

test("MonthOfYear.fromJson(plane object)", () => {
    expect(MonthOfYear.fromJson(null)).toBeNull()
    expect(MonthOfYear.fromJson(undefined)).toBeNull()
    const plainObj = {
        month: 10,
        year: 2020
    }
    expect(MonthOfYear.fromJson(plainObj)).toStrictEqual(new MonthOfYear(10, 2020))
})

test("MonthOfYear.fromJson(plain array)", () => {
    expect(MonthOfYear.fromJson([null])).toStrictEqual([null])

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

    let actual = MonthOfYear.fromJson(plainArray);
    expect(actual).toStrictEqual([
        new MonthOfYear(10, 2020), null, new MonthOfYear(1, 2019)
    ])

})