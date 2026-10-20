import { Link } from "react-router";
import useAuth from "@/auth/store";
import { Button } from "@/components/ui/button";

function Navbar() {
  const authStatus = useAuth((state) => state.authStatus);
  const logout = useAuth((state) => state.logout);

  return (
    <nav className="flex items-center justify-between border-b px-6 py-4">
      <Link to="/" className="font-semibold">
        Authentication App
      </Link>
      <div className="flex items-center gap-4">
        <Link to="/services">Services</Link>
        <Link to="/about">About</Link>
        {authStatus ? (
          <>
            <Link to="/dashboard">Dashboard</Link>
            <Button variant="outline" onClick={() => void logout()}>
              Log out
            </Button>
          </>
        ) : (
          <>
            <Link to="/login">Log in</Link>
            <Button
              nativeButton={false}
              render={<Link to="/signup" />}
            >
              Sign up
            </Button>
          </>
        )}
      </div>
    </nav>
  );
}

export default Navbar;
