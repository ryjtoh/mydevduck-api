// lib/db/models/User.ts
import mongoose, { Schema, Model } from "mongoose";

export interface IUser {
  email: string;
  name: string;
  githubId?: string;
  createdAt: Date;
}

const UserSchema = new Schema<IUser>(
  {
    email: {
      type: String,
      required: true,
      unique: true,
      lowercase: true,
    },
    name: {
      type: String,
      required: true,
    },
    githubId: {
      type: String,
      unique: true,
      sparse: true, // Allows multiple null values
    },
  },
  {
    timestamps: true, // Automatically adds createdAt and updatedAt
  }
);

// Prevent model recompilation during hot reload
const User: Model<IUser> =
  mongoose.models.User || mongoose.model<IUser>("User", UserSchema);

export default User;
