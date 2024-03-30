import { HamburgerIcon } from "@chakra-ui/icons";
import { IconButton, VStack, SlideFade, Tooltip } from "@chakra-ui/react";
import { useState } from "react";
import { Link as ReactRouterLink } from "react-router-dom";
import HDDIcon from "../../assets/icons/HDDIcon";
import ShareIcon from "../../assets/icons/ShareIcon";

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
			<SlideFade in={isMenuActive} unmountOnExit>
				<VStack mb={2}>
					<Tooltip label="Shared With Me" hasArrow placement="right">
						<IconButton
							icon={<ShareIcon />}
							isRound
							size="md"
							colorScheme="blue"
							variant="outline"
							as={ReactRouterLink}
							to="/sharedWithMe"
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
						/>
					</Tooltip>
				</VStack>
			</SlideFade>
			<Tooltip label="Navigate" hasArrow placement="right">
				<IconButton
					icon={<HamburgerIcon />}
					isRound
					size="lg"
					colorScheme="blue"
				/>
			</Tooltip>
		</VStack>
	);
};

export default NavButton;
