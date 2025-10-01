# MyDevDuck

A Next.js 14 application with TypeScript, Tailwind CSS, and shadcn/ui.

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

1. Clone the repository:

```bash
git clone https://github.com/ryjtoh/mydevduck.git
cd mydevduck
```

2. Install dependencies:

```bash
npm install
```

3. Set up environment variables:

```bash
cp .env.example .env
```

Edit `.env` and fill in your actual values for:

- MongoDB connection string
- Upstash Redis credentials
- Upstash Kafka credentials
- NextAuth configuration
- GitHub OAuth credentials

4. Run the development server:

```bash
npm run dev
```

Open [http://localhost:3000](http://localhost:3000) in your browser.

## Tech Stack

- **Framework**: Next.js 15 (App Router)
- **Language**: TypeScript (strict mode)
- **Styling**: Tailwind CSS v3
- **UI Components**: shadcn/ui
- **Code Quality**: ESLint + Prettier
- **Database**: MongoDB
- **Caching**: Upstash Redis
- **Event Streaming**: Upstash Kafka
- **Authentication**: NextAuth.js

## Folder Structure

```
mydevduck/
├── app/                          # Next.js App Router
│   ├── (dashboard)/              # Route group for dashboard pages
│   │   ├── layout.tsx            # Dashboard layout with sidebar
│   │   └── page.tsx              # Dashboard home page
│   ├── api/                      # API routes
│   │   ├── graphql/              # GraphQL endpoint
│   │   ├── webhooks/             # Webhook handlers
│   │   └── activities/           # Activity tracking endpoints
│   ├── globals.css               # Global styles with Tailwind
│   └── layout.tsx                # Root layout
├── components/                   # React components
│   ├── pet/                      # Pet-specific components (Tamagotchi)
│   ├── ui/                       # shadcn/ui components (reusable primitives)
│   └── dashboard/                # Dashboard-specific components
│       └── sidebar.tsx           # Navigation sidebar
├── lib/                          # Utility libraries
│   ├── db/                       # MongoDB connection and queries
│   ├── kafka/                    # Kafka producer/consumer
│   ├── redis/                    # Redis client configuration
│   ├── auth/                     # NextAuth configuration
│   ├── utils/                    # Shared utility functions
│   └── utils.ts                  # cn() for class merging
├── types/                        # TypeScript type definitions
├── public/                       # Static assets
│   └── assets/                   # Images, icons, etc.
└── ...config files
```

## Folder Structure Reasoning

### App Router Organization

- **`(dashboard)` Route Group**: Groups authenticated dashboard pages without adding `/dashboard` to the URL path. This keeps URLs clean while organizing related pages together.
- **`/api` Routes**: Separates API concerns into logical groups:
  - `/graphql` - GraphQL API for flexible data queries
  - `/webhooks` - External webhook handlers (GitHub, etc.)
  - `/activities` - Activity tracking and metrics

### Component Organization

- **`/components/pet`**: Pet-specific UI components for Tamagotchi features (pet display, interactions, status)
- **`/components/ui`**: shadcn/ui primitive components (buttons, cards, dialogs) - reusable across the app
- **`/components/dashboard`**: Dashboard-specific composed components (sidebar, header, widgets)

This separation follows the principle of **feature-based organization** at the top level and **reusability** at the component level.

### Lib Organization

Each integration gets its own module for clear separation of concerns:

- **`/lib/db`**: MongoDB connection pooling, schema models, and database queries
- **`/lib/kafka`**: Event streaming producers and consumers for activity tracking
- **`/lib/redis`**: Caching layer for session management and real-time features
- **`/lib/auth`**: NextAuth.js configuration and authentication helpers
- **`/lib/utils`**: Shared utility functions (date formatting, validation, etc.)

This structure promotes:

- **Testability**: Each module can be tested in isolation
- **Maintainability**: Easy to locate and update specific integrations
- **Scalability**: Simple to add new integrations without cluttering

## Available Scripts

- `npm run dev` - Start development server
- `npm run build` - Build for production
- `npm run start` - Start production server
- `npm run lint` - Run ESLint
- `npm run format` - Format code with Prettier

## License

ISC
