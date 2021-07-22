import MonthOfYearRange from "./MonthOfYearRange";
import MonthOfYear from "./MonthOfYear";

test("Simple creation", () => {
    const range = new MonthOfYearRange(new MonthOfYear(2, 2021), new MonthOfYear(7, 2021), null)
    expect(range.start).toStrictEqual(new MonthOfYear(2, 2021))
    expect(range.end).toStrictEqual(new MonthOfYear(7, 2021))
    expect(range.lastWithReadings).toBeNull()
})

test("Simple creation with plain args", () => {
    const range = new MonthOfYearRange({month: 2, year: 2021}, {month: 7, year: 2021}, {month: 6, year: 2021})
    expect(range.start).toStrictEqual(new MonthOfYear(2, 2021))
    expect(range.end).toStrictEqual(new MonthOfYear(7, 2021))
    expect(range.lastWithReadings).toStrictEqual(new MonthOfYear(6, 2021))
})

test("fromJson(null)", ()=>{
    expect(MonthOfYearRange.fromJson(null)).toBeNull()
})

test("fromJson(plainObject)", ()=>{
    const plain = [

    ]
    throw "Not implemented"
})

test("fromJson(plainArray)", ()=>{
    const plain = [

    ]
    throw "Not implemented"
})