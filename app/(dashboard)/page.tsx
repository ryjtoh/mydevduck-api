export default function DashboardPage() {
  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-3xl font-bold tracking-tight">Dashboard</h1>
        <p className="text-muted-foreground">
          Welcome to your MyDevDuck dashboard
        </p>
      </div>

      <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        <div className="rounded-lg border bg-card p-6 text-card-foreground shadow-sm">
          <h3 className="text-lg font-semibold">Your Pet</h3>
          <p className="text-sm text-muted-foreground">
            Check on your dev companion
          </p>
        </div>

        <div className="rounded-lg border bg-card p-6 text-card-foreground shadow-sm">
          <h3 className="text-lg font-semibold">Activities</h3>
          <p className="text-sm text-muted-foreground">
            Recent coding activities
          </p>
        </div>

        <div className="rounded-lg border bg-card p-6 text-card-foreground shadow-sm">
          <h3 className="text-lg font-semibold">Stats</h3>
          <p className="text-sm text-muted-foreground">
            Your development stats
          </p>
        </div>
      </div>
    </div>
  );
}
