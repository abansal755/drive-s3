import { HamburgerIcon } from "@chakra-ui/icons";
import { Tooltip } from "@chakra-ui/react";
import { VStack, IconButton } from "../common/framerMotionWrappers";
import { useState } from "react";
import { Link as ReactRouterLink } from "react-router-dom";
import HDDIcon from "../../assets/icons/HDDIcon";
import ShareIcon from "../../assets/icons/ShareIcon";
import { AnimatePresence } from "framer-motion";

const NavButton = () => {
	const [isMenuActive, setIsMenuActive] = useState(false);

	return (
		<VStack
			position="absolute"
			bottom={4}
			left={4}
			onMouseEnter={() => setIsMenuActive(true)}
			onMouseLeave={() => setIsMenuActive(false)}
		>
			<AnimatePresence>
				{isMenuActive && (
					<VStack
						mb={2}
						initial={{ opacity: 0, y: "40%" }}
						animate={{ opacity: 1, y: 0, scale: 1 }}
						exit={{ opacity: 0, scale: 0 }}
					>
						<Tooltip
							label="Shared With Me"
							hasArrow
							placement="right"
						>
							<IconButton
								icon={<ShareIcon />}
								isRound
								size="md"
								colorScheme="blue"
								variant="outline"
								as={ReactRouterLink}
								to="/sharedWithMe"
								whileHover={{ scale: 1.05 }}
							/>
						</Tooltip>
						<Tooltip label="My Drive" hasArrow placement="right">
							<IconButton
								icon={<HDDIcon />}
								isRound
								size="md"
								colorScheme="blue"
								variant="outline"
								as={ReactRouterLink}
								to="/"
								whileHover={{ scale: 1.05 }}
							/>
						</Tooltip>
					</VStack>
				)}
			</AnimatePresence>
			<Tooltip label="Navigate" hasArrow placement="right">
				<IconButton
					icon={<HamburgerIcon />}
					isRound
					size="lg"
					colorScheme="blue"
					variants={{
						active: { scale: 1.05 },
						inactive: { scale: 1 },
					}}
					animate={isMenuActive ? "active" : "inactive"}
				/>
			</Tooltip>
		</VStack>
	);
};

export default NavButton;
