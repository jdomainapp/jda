export function enum2Array<T>(enums: any): T[] {
  return Object.values(enums)
    .filter(t => typeof t === 'string')
    .map<T>(e => e as T);
}
