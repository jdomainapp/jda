export function assertNotNullOrUndefined<T>(value: T, debugLabel: string): asserts value is NonNullable<T> {
    if (value === null || value === undefined) {
        throw new Error(`${debugLabel} is undefined or null.`);
    }
}

export function assertStringIsNotEmpty(value: any, debugLabel: string): asserts value is string {
    if (typeof value !== 'string') {
        throw new Error(`${debugLabel} is not string`);
    }
    if (value.trim() === '') {
        throw new Error(`${debugLabel} cannot be empty`);
    }
}
