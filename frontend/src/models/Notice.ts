import type {INotice} from "../Interfaces/INotice";

export class Notice implements INotice {
    id: number;
    title: string;
    tag: string;
    updatedAt: string;
    views: number;
  
    constructor(data: Partial<INotice>) {
      this.id = data.id ?? 0;
      this.title = data.title ?? "Untitled Notice";
      this.tag = data.tag ?? "GENERAL";
      this.updatedAt = data.updatedAt ?? new Date().toISOString();
      this.views = data.views ?? 0;
    }
  
    // Helper method to format the date for display
    getFormattedDate(): string {
      return new Date(this.updatedAt).toLocaleDateString();
    }
  }
  