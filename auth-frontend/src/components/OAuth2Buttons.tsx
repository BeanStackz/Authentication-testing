import { Github, Chrome } from "lucide-react";
import { Button } from "@/components/ui/button";

const oauthBaseUrl =
  import.meta.env.VITE_OAUTH_BASE_URL || "http://localhost:8080";

function OAuth2Buttons() {
  const startOAuthLogin = (provider: "google" | "github") => {
    window.location.assign(`${oauthBaseUrl}/oauth2/authorization/${provider}`);
  };

  return (
    <div className="grid grid-cols-2 gap-3">
      <Button
        type="button"
        variant="outline"
        onClick={() => startOAuthLogin("google")}
      >
        <Chrome />
        Google
      </Button>
      <Button
        type="button"
        variant="outline"
        onClick={() => startOAuthLogin("github")}
      >
        <Github />
        GitHub
      </Button>
    </div>
  );
}

export default OAuth2Buttons;
