import {uri} from "./urlUtils";

test("uri tests", () => {
    expect(uri()).toBe("")
    expect(uri("http://localhost:8081/")).toBe("http://localhost:8081/")
    expect(uri("http://localhost:8081")).toBe("http://localhost:8081")

    expect(uri("http://localhost:8081", "api")).toBe("http://localhost:8081/api")
    expect(uri("http://localhost:8081/", "api")).toBe("http://localhost:8081/api")
    expect(uri("http://localhost:8081", "/api")).toBe("http://localhost:8081/api")
    expect(uri("http://localhost:8081/", "/api")).toBe("http://localhost:8081/api")
    expect(uri("http://localhost:8081/", "/api/")).toBe("http://localhost:8081/api/")

    expect(uri("http://localhost:8081/", "/api/", "/dd")).toBe("http://localhost:8081/api/dd")
    expect(uri("http://localhost:8081/", "/api/", "/dd/")).toBe("http://localhost:8081/api/dd/")
})