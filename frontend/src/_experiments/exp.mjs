import _ from "lodash"

let data = {
    organizations: [1, 2, 3]
}
console.log(data?.organizations)

data = null
console.log(data?.organizations ?? [4, 5, 6])