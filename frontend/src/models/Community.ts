
export class Community {
    id: number;
    title: string;
    tag: string;
    updatedAt: string;
    views: number;
    commentCount: number;
    answerCount: number;
  
    constructor(data: Community) {
      this.id = data.id ?? 0;
      this.title = data.title ?? "Untitled Notice";
      this.tag = data.tag ?? "GENERAL";
      this.updatedAt = data.updatedAt ?? new Date().toISOString();
      this.views = data.views ?? 0;
      this.commentCount = data.commentCount ?? 0;
      this.answerCount = data.answerCount ?? 0;
    }
  
    // Helper method to format the date for display
    getFormattedDate(): string {
      return new Date(this.updatedAt).toLocaleDateString();
    }
  }
  