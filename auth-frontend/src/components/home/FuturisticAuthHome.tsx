import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";
import { motion } from "framer-motion";
import {
  ArrowRight,
  Check,
  ChevronRight,
  Fingerprint,
  KeyRound,
  Lock,
  ShieldCheck,
  Sparkles,
  UserRound,
} from "lucide-react";
import { Link } from "react-router";

export default function FuturisticAuthHome() {
  return (
    <div className="relative min-h-screen overflow-hidden bg-background text-foreground">
      <div className="pointer-events-none absolute inset-x-0 top-0 -z-0 h-[520px] bg-[radial-gradient(circle_at_50%_-20%,color-mix(in_oklch,var(--primary)_16%,transparent),transparent_65%)]" />

      <main className="relative z-10 mx-auto max-w-6xl px-5 pb-20 pt-16 sm:px-8 lg:pt-24">
        <section className="grid items-center gap-14 lg:grid-cols-[1.05fr_0.95fr]">
          <div>
            <motion.div
              initial={{ opacity: 0, y: 12 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.6 }}
              className="mb-7 inline-flex items-center gap-2 rounded-full border border-border bg-card/60 px-3 py-1.5 text-xs font-medium text-muted-foreground shadow-sm backdrop-blur"
            >
              <span className="size-1.5 rounded-full bg-emerald-500 shadow-[0_0_10px_theme(colors.emerald.500)]" />
              Authentication that stays out of the way
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.7 }}
            >
              <p className="mb-4 text-sm font-semibold uppercase tracking-[0.24em] text-muted-foreground">
                Your app&apos;s secure front door
              </p>
              <div className="max-w-3xl text-5xl font-bold leading-[1.05] tracking-tight text-foreground sm:text-6xl lg:text-7xl">
                A simpler way to{" "}
                <span className="text-muted-foreground">secure every sign-in.</span>
              </div>
              <p className="mt-6 max-w-xl text-base leading-7 text-muted-foreground sm:text-lg">
                Give your users a fast, familiar login experience backed by
                secure sessions, OAuth2 providers, and a personal account
                dashboard.
              </p>
            </motion.div>

            <motion.div
              initial={{ opacity: 0, y: 18 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.2, duration: 0.6 }}
              className="mt-9 flex flex-col gap-3 sm:flex-row"
            >
              <Button
                nativeButton={false}
                size="lg"
                className="h-11 rounded-xl px-6"
                render={<Link to="/signup" />}
              >
                Create your account
                <ArrowRight />
              </Button>
              <Button
                size="lg"
                variant="outline"
                className="h-11 rounded-xl px-6"
                nativeButton={false}
                render={<Link to="/login" />}
              >
                Sign in
              </Button>
            </motion.div>

            <div className="mt-8 flex flex-wrap gap-x-6 gap-y-2 text-sm text-muted-foreground">
              {["JWT sessions", "OAuth2 ready", "Protected profiles"].map((detail) => (
                <span key={detail} className="inline-flex items-center gap-2">
                  <Check className="size-4 text-emerald-500" />
                  {detail}
                </span>
              ))}
            </div>
          </div>

          <motion.div
            initial={{ opacity: 0, scale: 0.96, y: 20 }}
            animate={{ opacity: 1, scale: 1, y: 0 }}
            transition={{ delay: 0.15, duration: 0.7 }}
            className="relative"
          >
            <div className="absolute -inset-5 rounded-[2rem] bg-primary/10 blur-3xl" />
            <Card className="relative overflow-hidden rounded-[1.5rem] border-border bg-card/75 p-2 shadow-2xl backdrop-blur-xl">
              <CardContent className="rounded-[1.15rem] border border-border/70 bg-background/70 p-5 sm:p-7">
                <div className="mb-8 flex items-center justify-between">
                  <div>
                    <p className="text-xs font-medium uppercase tracking-[0.18em] text-muted-foreground">
                      Security overview
                    </p>
                    <p className="mt-1 text-lg font-semibold">Your account is protected</p>
                  </div>
                  <div className="flex size-10 items-center justify-center rounded-xl bg-emerald-500/10 text-emerald-500">
                    <ShieldCheck className="size-5" />
                  </div>
                </div>

                <div className="rounded-xl border border-border bg-card/80 p-4">
                  <div className="flex items-center gap-3">
                    <div className="flex size-10 items-center justify-center rounded-full bg-primary text-primary-foreground">
                      <UserRound className="size-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="truncate text-sm font-medium">Personal workspace</p>
                      <p className="truncate text-xs text-muted-foreground">Last sign-in · Just now</p>
                    </div>
                    <span className="rounded-full bg-emerald-500/10 px-2 py-1 text-[11px] font-medium text-emerald-500">
                      Active
                    </span>
                  </div>
                </div>

                <div className="mt-5 grid grid-cols-2 gap-3">
                  {[
                    { icon: KeyRound, label: "Access token", value: "Ready" },
                    { icon: Lock, label: "Refresh session", value: "Encrypted" },
                  ].map(({ icon: Icon, label, value }) => (
                    <div key={label} className="rounded-xl border border-border/80 p-3">
                      <Icon className="mb-5 size-4 text-muted-foreground" />
                      <p className="text-xs text-muted-foreground">{label}</p>
                      <p className="mt-1 text-sm font-medium">{value}</p>
                    </div>
                  ))}
                </div>

                <div className="mt-5 flex items-center gap-2 rounded-xl bg-muted/60 px-3 py-2.5 text-xs text-muted-foreground">
                  <Fingerprint className="size-4" />
                  Built for secure, frictionless access
                </div>
              </CardContent>
            </Card>
          </motion.div>
        </section>

        <section className="mt-24">
          <div className="mb-8 flex items-end justify-between gap-4">
            <div>
              <p className="text-sm font-semibold uppercase tracking-[0.2em] text-muted-foreground">
                Everything you need
              </p>
              <h2 className="mt-3 text-3xl font-bold tracking-tight sm:text-4xl">
                Secure by design.
              </h2>
            </div>
            <Link
              to="/services"
              className="hidden items-center gap-1 text-sm font-medium text-muted-foreground transition-colors hover:text-foreground sm:flex"
            >
              Explore services <ChevronRight className="size-4" />
            </Link>
          </div>

          <div className="grid gap-4 md:grid-cols-3">
            {[
              {
                icon: ShieldCheck,
                title: "Protected sessions",
                description: "Access and refresh tokens work together to keep sessions reliable and secure.",
              },
              {
                icon: Sparkles,
                title: "OAuth2 sign-in",
                description: "Let users sign in with the providers they already trust, without extra friction.",
              },
              {
                icon: UserRound,
                title: "Personal dashboard",
                description: "Give every user a clear place to view their account and manage profile details.",
              },
            ].map(({ icon: Icon, title, description }, index) => (
              <motion.div
                key={title}
                initial={{ opacity: 0, y: 16 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1, duration: 0.5 }}
                viewport={{ once: true }}
              >
                <Card className="h-full rounded-2xl border-border bg-card/60 shadow-sm backdrop-blur transition-colors hover:bg-card/90">
                  <CardContent className="p-6">
                    <div className="mb-8 flex size-10 items-center justify-center rounded-xl bg-primary/10 text-primary">
                      <Icon className="size-5" />
                    </div>
                    <h3 className="text-lg font-semibold">{title}</h3>
                    <p className="mt-2 text-sm leading-6 text-muted-foreground">{description}</p>
                  </CardContent>
                </Card>
              </motion.div>
            ))}
          </div>
        </section>

        <section className="mt-24 rounded-2xl border border-border bg-card/55 p-7 shadow-sm backdrop-blur sm:p-10">
          <div className="flex flex-col items-start justify-between gap-6 sm:flex-row sm:items-center">
            <div>
              <p className="text-2xl font-semibold tracking-tight">Ready to make access feel effortless?</p>
              <p className="mt-2 text-sm text-muted-foreground">
                Start with a secure account and explore the dashboard.
              </p>
            </div>
            <Button
              nativeButton={false}
              className="h-11 shrink-0 rounded-xl px-6"
              render={<Link to="/signup" />}
            >
              Get started <ArrowRight />
            </Button>
          </div>
        </section>
      </main>

      <footer className="border-t border-border px-5 py-8 text-center text-sm text-muted-foreground">
        Secure access for modern applications · {new Date().getFullYear()}
      </footer>
    </div>
  );
}
