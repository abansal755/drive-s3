import {
	Text,
	CheckCircleIcon,
	CopyIcon,
	Button,
	Box,
} from "../../common/framerMotionWrappers";
import { useCopyToClipboard, useMeasure } from "@uidotdev/usehooks";
import { useEffect, useState } from "react";

const variants = {
	visible: { opacity: 1, scale: 1 },
	hidden: { opacity: 0, scale: 0 },
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

	const [ref, { width }] = useMeasure();

	return (
		<Button
			colorScheme={isClicked ? "green" : "blue"}
			mr={2}
			onClick={btnClickHandler}
		>
			<Box
				mr={2}
				animate={{ width: width ? width : "auto" }}
				overflow="hidden"
			>
				<Text
					ref={ref}
					w="fit-content"
					animate={{ opacity: [0, 1] }}
					key={isClicked}
				>
					{!isClicked && "Copy Link"}
					{isClicked && "Copied Link"}
				</Text>
			</Box>
			<Box display="flex" alignItems="center">
				<CopyIcon
					boxSize={4}
					variants={variants}
					animate={isClicked ? "hidden" : "visible"}
					position="absolute"
				/>
				<CheckCircleIcon
					boxSize={4}
					variants={variants}
					animate={isClicked ? "visible" : "hidden"}
				/>
			</Box>
		</Button>
	);
};

export default CopyLinkButton;
