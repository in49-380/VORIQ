import { FaGithub } from "react-icons/fa";
import Button from "./Button";

const GitHubButton = ({ref, title }) => {
  return (
    <Button
      tabIndex="0"
      title="GitHub"
      ref={ref}
      onClick={() =>
          window.open("https://github.com/in49-380/VORIQ", "_blank")
        }
      className="git-hub-button"
      aria-label={title}
    >
      <FaGithub />
    </Button>
  );
};
export default GitHubButton;
