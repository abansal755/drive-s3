import { CheckCircleIcon, ExternalLinkIcon } from "@chakra-ui/icons";
import { Box, Button } from "@chakra-ui/react";
import { useCopyToClipboard } from "@uidotdev/usehooks";
import { useEffect, useState } from "react";

const Icon = ({ isClicked }) => {
	return (
		<Box display="block" position="relative" bottom={2} right={1} ml={1}>
			<CheckCircleIcon
				position="absolute"
				opacity={isClicked ? 1 : 0}
				transition="300ms"
				transform={`rotate(${isClicked ? 0 : -180}deg)`}
			/>
			<ExternalLinkIcon
				position="absolute"
				opacity={isClicked ? 0 : 1}
				transition="300ms"
				transform={`rotate(${isClicked ? 180 : 0}deg)`}
			/>
		</Box>
	);
};

const CopyLinkButton = ({ copyValue }) => {
	const copyToClipboard = useCopyToClipboard()[1];
	const [isClicked, setIsClicked] = useState(false);

	useEffect(() => {
		if (isClicked) setTimeout(() => setIsClicked(false), 3000);
	}, [isClicked]);

	const btnClickHandler = () => {
		copyToClipboard(copyValue);
		setIsClicked(true);
	};

	return (
		<Button
			colorScheme={isClicked ? "green" : "blue"}
			rightIcon={<Icon isClicked={isClicked} />}
			mr={2}
			onClick={btnClickHandler}
			pr={6}
			transition="300ms"
		>
			{isClicked ? "Copied Link" : "Copy Link"}
		</Button>
	);
};

export default CopyLinkButton;
