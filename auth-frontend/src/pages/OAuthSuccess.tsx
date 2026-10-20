import useAuth from "@/auth/store";
import { Spinner } from "@/components/ui/spinner";
import { refreshToken } from "@/services/AuthService";
import { useEffect } from "react";
import toast from "react-hot-toast";
import { useNavigate } from "react-router";

function OAuthSuccess() {
  const changeLocalLoginData = useAuth((state) => state.changeLocalLoginData);
  const navigate = useNavigate();

  useEffect(() => {
    async function getAccessToken() {
      try {
        const responseLoginData = await refreshToken();
        changeLocalLoginData(
          responseLoginData.accessToken,
          responseLoginData.user,
          true,
        );

        toast.success("Login success !");
        navigate("/dashboard");
      } catch (error) {
        toast.error("Error while login!");
        console.log(error);
      }
    }

    void getAccessToken();
  }, [changeLocalLoginData, navigate]);

  return (
    <div className="p-10 flex flex-col gap-3 justify-center items-center">
      <Spinner />
      <h1 className="text-2xl font-semibold">Please wait....</h1>
    </div>
  );
}

export default OAuthSuccess;
