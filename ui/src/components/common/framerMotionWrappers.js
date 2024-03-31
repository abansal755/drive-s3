import {
	Button as ChakraButton,
	Text as ChakraText,
	HStack as ChakraHStack,
	Badge as ChakraBadge,
	IconButton as ChakraIconButton,
	Alert as ChakraAlert,
	VStack as ChakraVStack,
	Tr as ChakraTr,
	Stack as ChakraStack,
	Heading as ChakraHeading,
	Container as ChakraContainer,
	WrapItem as ChakraWrapItem,
} from "@chakra-ui/react";
import {
	CheckCircleIcon as ChakraCheckCircleIcon,
	ExternalLinkIcon as ChakraExternalLinkIcon,
	CopyIcon as ChakraCopyIcon,
	TriangleUpIcon as ChakraTriangleUpIcon,
	TriangleDownIcon as ChakraTriangleDownIcon,
	ChevronDownIcon as ChakraChevronDownIcon,
} from "@chakra-ui/icons";
import { motion } from "framer-motion";

export const Button = motion(ChakraButton);
export const Text = motion(ChakraText);
export const CheckCircleIcon = motion(ChakraCheckCircleIcon);
export const ExternalLinkIcon = motion(ChakraExternalLinkIcon);
export const CopyIcon = motion(ChakraCopyIcon);
export const HStack = motion(ChakraHStack);
export const Badge = motion(ChakraBadge);
export const IconButton = motion(ChakraIconButton);
export const TriangleUpIcon = motion(ChakraTriangleUpIcon);
export const TriangleDownIcon = motion(ChakraTriangleDownIcon);
export const Alert = motion(ChakraAlert);
export const VStack = motion(ChakraVStack);
export const Tr = motion(ChakraTr);
export const ChevronDownIcon = motion(ChakraChevronDownIcon);
export const Stack = motion(ChakraStack);
export const Heading = motion(ChakraHeading);
export const Container = motion(ChakraContainer);
export const WrapItem = motion(ChakraWrapItem);
