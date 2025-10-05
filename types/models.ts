// types/models.ts
import { Types } from "mongoose";

// Re-export all model interfaces
export type { IUser } from "@/lib/db/models/User";
export type { IPet } from "@/lib/db/models/Pet";
export type { IActivity } from "@/lib/db/models/Activity";
export type { IFoodInventory, FoodTypes } from "@/lib/db/models/FoodInventory";

// Utility types
export type ActivityType = "leetcode" | "github" | "youtube" | "mcq";
export type FoodType = "apple" | "steak" | "berry" | "biscuit";
export type EvolutionStage = "egg" | "baby" | "teen" | "adult" | "elite";

// API response types
export interface CreateActivityDTO {
  type: ActivityType;
  foodGenerated: number;
  foodType: FoodType;
  metadata?: Record<string, any>;
}

export interface FeedPetDTO {
  petId: string;
  foodType: FoodType;
  amount: number;
}
