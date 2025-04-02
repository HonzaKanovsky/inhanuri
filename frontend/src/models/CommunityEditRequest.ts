import Community from "@/components/Community.vue";
import {FileData} from "@/models/FileData"

export class CommunityEditRequest {
    id: number;
    title: string;
    content: string;
    tag: string;
    updatedAt: string;
    views: number;
    files:FileData[];
    token:String;
  
    constructor(data: CommunityEditRequest) {
      this.id = data.id ?? 0;
      this.title = data.title ?? "Untitled Notice";
      this.content = data.content ?? "";
      this.tag = data.tag ?? "GENERAL";
      this.updatedAt = data.updatedAt ?? new Date().toISOString();
      this.views = data.views ?? 0;
      this.files = data.files ?? [];
      this.token = data.token ?? "";
    }
  
    // Helper method to format the date for display
    getFormattedDate(): string {
      return new Date(this.updatedAt).toLocaleDateString();
    }
  }
  