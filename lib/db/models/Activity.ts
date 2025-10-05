// lib/db/models/Activity.ts
import mongoose, { Schema, Model, Types, Document } from "mongoose";

// TypeScript Interface
export interface IActivity extends Document {
  _id: Types.ObjectId;
  userId: Types.ObjectId;
  type: "leetcode" | "github" | "youtube" | "mcq";
  foodGenerated: number;
  foodType: "apple" | "steak" | "berry" | "biscuit";
  metadata: Record<string, any>; // Flexible object for any additional data
  timestamp: Date;
  createdAt: Date;
  updatedAt: Date;
}

// Mongoose Schema
const ActivitySchema = new Schema<IActivity>(
  {
    userId: {
      type: Schema.Types.ObjectId,
      ref: "User",
      required: true,
      index: true, // Index for fast queries by user
    },
    type: {
      type: String,
      enum: ["leetcode", "github", "youtube", "mcq"],
      required: true,
    },
    foodGenerated: {
      type: Number,
      required: true,
      min: 0,
      default: 0,
    },
    foodType: {
      type: String,
      enum: ["apple", "steak", "berry", "biscuit"],
      required: true,
    },
    metadata: {
      type: Schema.Types.Mixed, // Allows any object structure
      default: {},
    },
    timestamp: {
      type: Date,
      default: Date.now,
      index: true, // Index for sorting/filtering by time
    },
  },
  {
    timestamps: true, // Auto-adds createdAt and updatedAt
  }
);

// Compound index for efficient queries like "get user's recent activities"
ActivitySchema.index({ userId: 1, timestamp: -1 });

// Model export with hot-reload protection
const Activity: Model<IActivity> =
  mongoose.models.Activity ||
  mongoose.model<IActivity>("Activity", ActivitySchema);

export default Activity;
