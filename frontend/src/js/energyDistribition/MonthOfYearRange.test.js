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

test("fromPlainObject((null)", () => {
    expect(MonthOfYearRange.fromPlainObject(null)).toBeNull()
})

test("fromPlainObject", () => {
    const plain = {
        start: {month: 2, year: 2021},
        end: {month: 7, year: 2021},
        lastWithReadings: {month: 6, year: 2021}
    }
    expect(MonthOfYearRange.fromPlainObject(plain))
        .toStrictEqual(
            new MonthOfYearRange({month: 2, year: 2021}, {month: 7, year: 2021}, {month: 6, year: 2021})
        )
})

test("toInstance", () => {
    expect(MonthOfYearRange.toInstance(null)).toBeNull();
    (() => {
        const inst = new MonthOfYearRange({month: 2, year: 2021}, {month: 7, year: 2021}, {month: 6, year: 2021})
        expect(MonthOfYearRange.toInstance(inst) === inst).toBe(true)
    })();

    (() => {
        const obj = {
            start: {month: 2, year: 2021},
            end: {month: 7, year: 2021},
            lastWithReadings: {month: 6, year: 2021}
        }

        const inst = new MonthOfYearRange({month: 2, year: 2021}, {month: 7, year: 2021}, {month: 6, year: 2021})
        expect(MonthOfYearRange.toInstance(obj) === obj).toBe(false)
        expect(MonthOfYearRange.toInstance(obj)).toStrictEqual(inst)
    })();
})

test("toMonthsList in same year", () => {
    expect(
        new MonthOfYearRange({month: 2, year: 2021}, {month: 7, year: 2021}, {month: 6, year: 2021})
            .toMonthsList()
    ).toStrictEqual(
        [
            new MonthOfYear(7, 2021),
            new MonthOfYear(6, 2021),
            new MonthOfYear(5, 2021),
            new MonthOfYear(4, 2021),
            new MonthOfYear(3, 2021),
            new MonthOfYear(2, 2021)
        ]
    )
})

test("toMonthsList in two years", () => {
    expect(
        new MonthOfYearRange({month: 10, year: 2020}, {month: 2, year: 2021}, {month: 1, year: 2021})
            .toMonthsList()
    ).toStrictEqual(
        [
            new MonthOfYear(2, 2021),
            new MonthOfYear(1, 2021),
            new MonthOfYear(12, 2020),
            new MonthOfYear(11, 2020),
            new MonthOfYear(10, 2020)
        ]
    )
})