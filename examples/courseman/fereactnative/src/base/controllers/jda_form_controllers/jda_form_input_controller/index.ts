export interface IJDAInput<T> {
  value?: T;
  onChange?: (value?: T) => void;
  disabled?: boolean;
  label?: string;
  error?: string;
}

export interface IJDAModuleInput<T> extends IJDAInput<T> {
  onCreate?: () => void;
  onEdit?: () => void;
  onShowDetail?: () => void;
  onUnlink?: () => void;
  onSearch?: (keyword: string) => Promise<T[]>;
  options: T[];
}

export interface IJDAMultiModuleInput<T> extends IJDAMultiInput<T> {
  onCreate?: () => void;
  onEdit?: (v: T) => void;
  onShowDetail?: (v: T) => void;
  onUnlink?: () => void;
  onSearch?: (keyword: string) => Promise<T[]>;
  options: T[];
}

export interface IJDAMultiInput<T> {
  values?: T[];
  onChange?: (values: T[]) => void;
  disabled?: boolean;
  label?: string;
}
