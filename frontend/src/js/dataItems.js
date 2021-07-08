import _ from 'lodash'

function makeItem(i) {
    return {
        firstName: `First name ${i}`,
        lastName: `Last name ${i}`
    }
}

function makeItems(n) {
    return _.range(1, n + 1).map((v) => makeItem(v))
}

export {makeItems, makeItem}