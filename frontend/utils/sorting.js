export function sortBy(key, isAsc = true) {
    return (a, b) => {

        const aVal = a[key];
        const bVal = b[key];

        if (typeof aVal === "string" && typeof bVal === "string") {
            const result = aVal.localeCompare(bVal);
            // if (isAsc) {
            //     return result;
            // } else {
            //     return -result;
            // }
            return isAsc ? result : -result;
        }

        if (typeof aVal === "number" && typeof bVal === "number") {
            const result = aVal - bVal;
            return isAsc ? result : -result;
        }

        return 0;
    }
}