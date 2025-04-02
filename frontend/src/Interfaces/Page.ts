export interface Page<T> {
    content: T[];
    number: number;      // Current page index
    totalPages: number;  // Total pages
  }
  